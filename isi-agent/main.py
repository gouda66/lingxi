from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import PydanticOutputParser
from pydantic import BaseModel, Field
from typing import List, Optional
from pypdf import PdfReader
from io import BytesIO
import asyncio

app = FastAPI(title="AI面试Agent")

# 大模型配置 - 优化参数提升速度
llm = ChatOpenAI(
    api_key="sk-42a2860dab6f4c10b2207b2178f64317",
    base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    model="qwen-plus",
    temperature=0.1,
    max_retries=2  # 失败重试次数
)

# -----------------------------------------------------------------------------
# 1. 简历解析 - AI提取原始信息
# -----------------------------------------------------------------------------
class EducationItem(BaseModel):
    school: str = Field(description="学校名称")
    major: str = Field(description="专业")
    degree: str = Field(description="学历/学位")
    startDate: str = Field(description="开始时间,如:2018-09或2018年9月")
    endDate: str = Field(description="结束时间,如:2022-06或2022年6月")

class ProjectItem(BaseModel):
    projectName: str = Field(description="项目名称")
    role: str = Field(description="担任角色")
    startDate: str = Field(description="开始时间")
    endDate: str = Field(description="结束时间")
    description: str = Field(description="项目描述")
    responsibilities: str = Field(description="个人职责")
    technologies: str = Field(description="使用的技术栈,逗号分隔")

class ResumeInfo(BaseModel):
    # 基础信息
    fullName: str = Field(description="姓名,从简历中提取,如无则留空")
    gender: Optional[str] = Field(description="性别:男/女,从简历中提取,如无则留空")
    birthDate: str = Field(description="出生日期,格式如:1995-06-15或1995年6月,如无则留空")
    phone: str = Field(description="手机号,从简历中提取原文,如无则留空")
    email: str = Field(description="邮箱,从简历中提取原文,如无则留空")
    location: str = Field(description="所在地/现居地,从简历中提取,如无则留空")
    jobTitle: str = Field(description="求职意向/目标职位,从简历中提取原文,如无则留空")
    expectedSalary: str = Field(description="期望薪资,从简历中提取原文,如无则留空")
    selfEvaluation: str = Field(description="自我评价/个人评价,从简历中提取原文,不做修改")
    
    # 教育背景(JSON数组)
    educationJson: List[EducationItem] = Field(description="教育背景列表,每段经历包含学校、专业、学历、起止时间")
    
    # 工作年限
    workYears: Optional[int] = Field(description="工作年限,数字类型,如无则留空")
    
    # 技能列表(用于存入resume_skill表)
    skills: List[str] = Field(description="技能列表,从简历中提取的技术栈原文,不做修改")
    
    # 工作经历(原文)
    workExperience: List[str] = Field(description="工作经历,从简历中提取原文段落,不做修改")
    
    # 项目经验(结构化)
    projects: List[ProjectItem] = Field(description="项目经验列表,每个项目包含名称、角色、时间、描述、职责、技术栈")
    
    # 获奖情况
    awards: List[str] = Field(description="获奖情况/荣誉奖项,从简历中提取原文,不做修改")

resume_parser = PydanticOutputParser(pydantic_object=ResumeInfo)
resume_prompt = ChatPromptTemplate.from_messages([
    ("system", """你是专业简历解析专家,从简历中提取信息并结构化。
重要规则:
1. **严格提取原文**,不做任何修改、总结或润色
2. 如果某个字段在简历中不存在,返回空字符串或空数组
3. 保持原文的措辞和格式
4. 日期格式尽量统一为 YYYY-MM 或 YYYY年MM月
5. 只输出JSON,无多余文字
{format_instructions}"""),
    ("user", "简历内容:\n{text}")
])
resume_chain = resume_prompt | llm | resume_parser

@app.post("/ai/parse-resume")
async def parse_resume(file: UploadFile = File(...)):
    # 验证文件类型
    if not file.filename or not file.filename.lower().endswith('.pdf'):
        raise HTTPException(status_code=400, detail="只支持PDF文件")
    
    try:
        # 读取文件内容
        contents = await file.read()
        
        # 验证PDF文件头
        if not contents.startswith(b'%PDF'):
            raise HTTPException(status_code=400, detail="文件格式错误,请上传有效的PDF文件")
        
        # 提取PDF文本
        reader = PdfReader(BytesIO(contents))
        text = "\n".join([page.extract_text() or "" for page in reader.pages])
        
        if not text.strip():
            raise HTTPException(status_code=400, detail="PDF文件中没有可提取的文本内容")
        
        # 限制文本长度,加速处理
        if len(text) > 5000:
            text = text[:5000]
        
        # 异步调用AI解析
        loop = asyncio.get_event_loop()
        result = await loop.run_in_executor(None, lambda: resume_chain.invoke({
            "text": text,
            "format_instructions": resume_parser.get_format_instructions()
        }))
        
        return result.dict()
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"PDF解析失败: {str(e)}")

# -----------------------------------------------------------------------------
# 2. 根据【岗位 + 简历】个性化出题（已修改）
# -----------------------------------------------------------------------------
class InterviewQuestion(BaseModel):
    type: str = Field(description="基础/框架/场景/架构/代码/项目")
    title: str = Field(description="题目")
    answer: str = Field(description="参考答案")

class QuestionList(BaseModel):
    questions: List[InterviewQuestion]

q_parser = PydanticOutputParser(pydantic_object=QuestionList)
q_prompt = ChatPromptTemplate.from_messages([
    ("system", """
你是专业技术面试官，根据**面试岗位**和**候选人简历**生成个性化面试题。
要求：
1. 贴合岗位要求，难度适中
2. 结合候选人项目经历提问
3. 覆盖基础、场景、架构、代码
4. 只输出JSON，无多余内容
{format_instructions}
"""),
    ("user", """
面试岗位：{jobPosition}
技能栈：{skills}
项目经历：{projects}
""")
])
q_chain = q_prompt | llm | q_parser

@app.post("/ai/generate-questions")
async def generate_questions(
    jobPosition: str = Form(...),
    skills: List[str] = Form(...),
    projects: List[str] = Form(...)
):
    loop = asyncio.get_event_loop()
    result = await loop.run_in_executor(None, lambda: q_chain.invoke({
        "jobPosition": jobPosition,
        "skills": skills,
        "projects": projects,
        "format_instructions": q_parser.get_format_instructions()
    }))
    return result.dict()

# -----------------------------------------------------------------------------
# 3. 实时六维评分
# -----------------------------------------------------------------------------
@app.post("/ai/score-answer")
async def score_answer(
    question: str = Form(...),
    standard_answer: str = Form(...),
    user_answer: str = Form(...)
):
    prompt = f"""
只输出6个0~10的数字，英文逗号分隔，无其他内容。
维度顺序：专业准确性,逻辑清晰度,知识深度,表达流畅度,项目结合度,拓展思考力

题目：{question}
参考答案：{standard_answer}
候选人回答：{user_answer}

输出示例：8,7,9,6,8,7
"""
    loop = asyncio.get_event_loop()
    resp = await loop.run_in_executor(None, llm.invoke, prompt)
    return {"scoreLine": resp.content.strip()}

# -----------------------------------------------------------------------------
# 4. 面试报告 Agent
# -----------------------------------------------------------------------------
class InterviewReport(BaseModel):
    totalScore: float
    level: str
    advantage: str
    disadvantage: str
    improvement: str
    fullReport: str

report_parser = PydanticOutputParser(pydantic_object=InterviewReport)
report_prompt = ChatPromptTemplate.from_messages([
    ("system", "你是资深技术面试官，生成专业面试报告，只输出JSON。{format_instructions}"),
    ("user", """
岗位：{jobPosition}
技能：{skills}
六维分数：{scoreJson}
问答记录：{qaHistory}
""")
])
report_chain = report_prompt | llm | report_parser

@app.post("/ai/generate-report")
async def generate_report(
    jobPosition: str = Form(...),
    skills: List[str] = Form(...),
    scoreJson: str = Form(...),
    qaHistory: str = Form(...)
):
    loop = asyncio.get_event_loop()
    result = await loop.run_in_executor(None, lambda: report_chain.invoke({
        "jobPosition": jobPosition,
        "skills": skills,
        "scoreJson": scoreJson,
        "qaHistory": qaHistory,
        "format_instructions": report_parser.get_format_instructions()
    }))
    return result.dict()