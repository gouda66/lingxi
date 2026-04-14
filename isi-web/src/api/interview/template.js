/**
 * 面试评估报告生成器 - 纯JavaScript方法
 * 用法：generateInterviewReport(csvString)
 */

export function parseInterviewData(csvData) {
    const fields = csvData.split(',');
    const clean = (str) => str ? str.trim().replace(/^["']|["']$/g, '') : '';

    return {
        id: clean(fields[0]) || 'Unknown',
        breadthScore: parseInt(fields[1]) || 0,
        depthScore: parseInt(fields[2]) || 0,
        duration: parseFloat(fields[4]) || 0,
        advantages: clean(fields[10]) || '',
        disadvantages: clean(fields[11]) || '',
        evaluation: clean(fields[13]) || '',
        date: clean(fields[16]) || new Date().toISOString()
    };
}

export function getScoreColor(score) {
    if (score >= 80) return '#28a745';
    if (score >= 60) return '#667eea';
    if (score >= 40) return '#ffc107';
    return '#dc3545';
}

export function extractSuggestions(evaluation) {
    if (!evaluation) return [];
    const match = evaluation.match(/建议(.+?)(?:$|。)/);
    if (match) {
        return match[1].split(/[；;]|同时/).map(s => s.trim()).filter(s => s.length > 3);
    }
    return [];
}

export function generateReportHTML(data) {
    const overall = Math.round(data.breadthScore * 0.4 + data.depthScore * 0.6);
    const suggestions = extractSuggestions(data.evaluation);
    const scores = [
        { label: '技术广度', value: data.breadthScore, color: getScoreColor(data.breadthScore) },
        { label: '技术深度', value: data.depthScore, color: getScoreColor(data.depthScore) },
        { label: '代码能力', value: Math.round(data.depthScore * 0.8), color: getScoreColor(data.depthScore * 0.8) },
        { label: '综合评分', value: overall, color: getScoreColor(overall) }
    ];

    return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>技术面试评估报告 - ${data.id}</title>
    <style>
        *{margin:0;padding:0;box-sizing:border-box}
        body{font-family:"Segoe UI","PingFang SC",sans-serif;background:linear-gradient(135deg,#f5f7fa 0%,#c3cfe2 100%);min-height:100vh;padding:40px 20px;color:#2c3e50;line-height:1.6}
        .container{max-width:1000px;margin:0 auto;background:white;border-radius:16px;box-shadow:0 10px 40px rgba(0,0,0,0.1);overflow:hidden}
        .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:40px;position:relative}
        .candidate-info{display:flex;align-items:center;gap:30px}
        .avatar{width:100px;height:100px;border-radius:50%;background:white;display:flex;align-items:center;justify-content:center;font-size:36px;color:#667eea;border:4px solid rgba(255,255,255,0.3)}
        .info-text h1{font-size:32px;margin-bottom:10px}
        .info-meta{display:flex;gap:20px;flex-wrap:wrap;opacity:0.95;font-size:14px}
        .score-overview{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:20px;padding:30px 40px;background:#f8f9fa;border-bottom:1px solid #e9ecef}
        .score-card{background:white;padding:20px;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.05);text-align:center}
        .score-value{font-size:36px;font-weight:bold;margin:10px 0}
        .score-label{color:#6c757d;font-size:14px}
        .score-bar{width:100%;height:6px;background:#e9ecef;border-radius:3px;margin-top:10px;overflow:hidden}
        .score-fill{height:100%;border-radius:3px}
        .content{padding:40px}
        .section{margin-bottom:35px}
        .section-title{font-size:20px;font-weight:600;color:#2c3e50;margin-bottom:20px;display:flex;align-items:center;gap:10px;padding-bottom:10px;border-bottom:2px solid #e9ecef}
        .section-icon{width:32px;height:32px;background:linear-gradient(135deg,#667eea,#764ba2);color:white;border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:16px}
        .evaluation-box{background:#f8f9fa;border-radius:12px;padding:20px;line-height:1.8;color:#495057;border-left:4px solid #667eea;text-align:justify}
        .cons-box{background:#f8d7da;border-radius:12px;padding:20px;line-height:1.8;color:#721c24;border-left:4px solid #dc3545;text-align:justify}
        .tag-container{display:flex;flex-wrap:wrap;gap:8px;margin-top:15px}
        .tag{padding:4px 12px;background:#e9ecef;border-radius:20px;font-size:12px;color:#495057}
        .tag.strong{background:#d4edda;color:#155724}
        .tag.tech{background:#e7f3ff;color:#004085}
        .suggestion-list{list-style:none;padding:0}
        .suggestion-list li{padding:15px;margin-bottom:10px;background:#e7f3ff;border-radius:8px;border-left:3px solid #667eea;position:relative;padding-left:40px}
        .suggestion-list li::before{content:'💡';position:absolute;left:12px;top:50%;transform:translateY(-50%)}
        .footer{background:#f8f9fa;padding:20px 40px;text-align:center;color:#6c757d;font-size:12px;border-top:1px solid #e9ecef}
        @media print{body{background:white;padding:0}.container{box-shadow:none;max-width:100%}}
        @media(max-width:768px){.candidate-info{flex-direction:column;text-align:center}.score-overview{grid-template-columns:1fr}}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="candidate-info">
                <div class="avatar">👨‍💻</div>
                <div class="info-text">
                    <h1>技术面试评估报告</h1>
                    <div class="info-meta">
                        <span>📅 ${new Date(data.date).toLocaleDateString('zh-CN')}</span>
                        <span>🎯 Java后端开发</span>
                        <span>⏱️ ${data.duration}分钟</span>
                        <span>🆔 ${data.id}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="score-overview">
            ${scores.map(s => `
                <div class="score-card">
                    <div class="score-label">${s.label}</div>
                    <div class="score-value" style="color:${s.color}">${s.value}</div>
                    <div class="score-bar"><div class="score-fill" style="width:${Math.min(s.value,100)}%;background:${s.color}"></div></div>
                </div>
            `).join('')}
        </div>
        <div class="content">
            <div class="section">
                <h2 class="section-title"><span class="section-icon">✓</span>核心优势</h2>
                <div class="evaluation-box">${data.advantages || '暂无数据'}</div>
                <div class="tag-container">
                    <span class="tag strong">Spring生态</span>
                    <span class="tag strong">微服务架构</span>
                    <span class="tag strong">AI集成</span>
                    <span class="tag tech">Redis</span>
                    <span class="tag tech">分布式事务</span>
                </div>
            </div>
            <div class="section">
                <h2 class="section-title"><span class="section-icon">⚠</span>关键不足</h2>
                <div class="cons-box">${data.disadvantages || '暂无数据'}</div>
            </div>
            <div class="section">
                <h2 class="section-title"><span class="section-icon">📝</span>综合评价</h2>
                <div class="evaluation-box">${data.evaluation || '暂无数据'}</div>
            </div>
            ${suggestions.length ? `
            <div class="section">
                <h2 class="section-title"><span class="section-icon">🎯</span>改进建议</h2>
                <ul class="suggestion-list">${suggestions.map(s => `<li>${s}</li>`).join('')}</ul>
            </div>` : ''}
        </div>
        <div class="footer">
            <p>生成时间：${new Date(data.date).toLocaleString('zh-CN')} | 面试官：技术委员会</p>
            <p style="margin-top:5px;color:#adb5bd">本报告仅供内部参考，请注意保密</p>
        </div>
    </div>
</body>
</html>`;
}

export function downloadHTML(content, filename) {
    const blob = new Blob([content], { type: 'text/html;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
}

/**
 * 主方法：传入CSV字符串直接下载报告
 * @param {string} csvString - CSV格式的评估数据
 */
export function generateInterviewReport(csvString) {
    if (!csvString || !csvString.trim()) {
        console.error('CSV数据不能为空');
        return;
    }

    try {
        const data = parseInterviewData(csvString);
        const html = generateReportHTML(data);
        const filename = `面试评估报告_${data.id}_${Date.now()}.html`;
        downloadHTML(html, filename);
        console.log('报告已生成:', filename);
        return { success: true, filename, data };
    } catch (error) {
        console.error('生成报告失败:', error);
        return { success: false, error };
    }
}

// 使用示例：
// generateInterviewReport('1,79,4,,72.00,,,,,,优势,不足,,评价,,2026-04-08');