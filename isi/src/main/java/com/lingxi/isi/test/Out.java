package com.lingxi.isi.test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库表结构导出工具
 * 将SQL文件中的表结构导出为Excel表格
 */
public class Out {

    public static void main(String[] args) {
        String sqlFilePath = "D:\\study_project\\lingxi\\isi\\src\\main\\resources\\db\\isi.sql";
        String excelFilePath = "D:\\study_project\\lingxi\\isi\\src\\main\\resources\\db\\table_structure.xlsx";

        try {
            String sqlContent = readFile(sqlFilePath);
            List<TableInfo> tables = parseSql(sqlContent);
            exportToExcel(tables, excelFilePath);

            System.out.println("导出成功！共导出 " + tables.size() + " 个表");
            for (TableInfo table : tables) {
                System.out.println("  - " + table.tableName + " (" + table.columns.size() + " 个字段)");
            }
            System.out.println("文件位置：" + excelFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static List<TableInfo> parseSql(String sql) {
        List<TableInfo> tables = new ArrayList<>();

        // 使用更精确的正则：匹配 CREATE TABLE 到 ); 之间的内容
        Pattern tablePattern = Pattern.compile(
                "CREATE\\s+TABLE\\s+`([^`]+)`\\s*\\(([\\s\\S]+?)\\)\\s*;",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = tablePattern.matcher(sql);

        while (matcher.find()) {
            String tableName = matcher.group(1);
            String columnsSql = matcher.group(2);

            TableInfo table = new TableInfo();
            table.tableName = tableName;
            table.comment = getTableComment(tableName);

            parseColumns(columnsSql, table);
            tables.add(table);
        }

        return tables;
    }

    private static String getTableComment(String tableName) {
        switch (tableName) {
            case "resume": return "简历主表";
            case "resume_skill": return "简历技能表";
            case "resume_project": return "简历项目经验表";
            case "interview_session": return "面试会话表";
            case "interview_question": return "面试题目表";
            case "interview_answer": return "面试回答表";
            case "interview_report": return "面试综合报告表";
            default: return tableName;
        }
    }

    private static void parseColumns(String columnsSql, TableInfo table) {
        // 按换行符分割，而不是逗号，避免字段定义中包含逗号
        String[] lines = columnsSql.split("\\n");

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("PRIMARY KEY") ||
                    line.startsWith("KEY") ||
                    line.startsWith("CONSTRAINT") ||
                    line.startsWith("--") ||
                    line.isEmpty()) {
                continue;
            }

            // 移除行尾的逗号
            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1).trim();
            }

            Pattern columnPattern = Pattern.compile(
                    "`([^`]+)`\\s+([^\\s(]+)(?:\\(([^)]+)\\))?\\s*(.*)",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = columnPattern.matcher(line);
            if (matcher.find()) {
                ColumnInfo column = new ColumnInfo();
                column.name = matcher.group(1);
                column.type = matcher.group(2).toLowerCase();
                column.length = matcher.group(3);

                String rest = matcher.group(4);
                if (rest != null && !rest.isEmpty()) {
                    List<String> constraints = new ArrayList<>();

                    if (rest.contains("NOT NULL")) {
                        constraints.add("Not Null");
                    }

                    if (rest.contains("AUTO_INCREMENT")) {
                        constraints.add("自增");
                    }

                    if (rest.contains("DEFAULT")) {
                        Pattern defaultPattern = Pattern.compile("DEFAULT\\s+'([^']+)'|DEFAULT\\s+(\\S+)");
                        Matcher defaultMatcher = defaultPattern.matcher(rest);
                        if (defaultMatcher.find()) {
                            String defaultVal = defaultMatcher.group(1) != null ?
                                    defaultMatcher.group(1) :
                                    defaultMatcher.group(2);
                            constraints.add("默认:" + defaultVal);
                        }
                    }

                    column.constraint = constraints.isEmpty() ? "Null" : String.join(", ", constraints);
                } else {
                    column.constraint = "Null";
                }

                column.comment = getColumnComment(column.name);

                table.columns.add(column);
            }
        }
    }

    private static String getColumnComment(String columnName) {
        switch (columnName) {
            case "id": return "主键ID";
            case "user_id": return "用户ID";
            case "resume_name": return "简历名称";
            case "candidate_name": return "候选人姓名";
            case "contact_info": return "联系方式";
            case "resume_id": return "简历ID";
            case "skill_name": return "技能名称";
            case "proficiency": return "熟练度";
            case "project_name": return "项目名称";
            case "role": return "担任角色";
            case "description": return "项目描述";
            case "session_code": return "会话编号";
            case "candidate_id": return "候选人ID";
            case "status": return "状态";
            case "session_id": return "会话ID";
            case "question_content": return "题目内容";
            case "difficulty": return "难度等级";
            case "sequence_no": return "序号";
            case "question_id": return "题目ID";
            case "answer_content": return "回答内容";
            case "ai_score": return "AI评分";
            case "total_score": return "总分";
            case "recommendation": return "录用建议";
            default: return "";
        }
    }

    private static void exportToExcel(List<TableInfo> tables, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);

            for (TableInfo table : tables) {
                String sheetName = table.comment != null && !table.comment.isEmpty() ?
                        table.comment : table.tableName;
                if (sheetName.length() > 31) {
                    sheetName = sheetName.substring(0, 31);
                }
                Sheet sheet = workbook.createSheet(sheetName);

                Row headerRow = sheet.createRow(0);
                String[] headers = {"字段", "类型", "长度", "约束", "备注"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                for (int i = 0; i < table.columns.size(); i++) {
                    ColumnInfo column = table.columns.get(i);
                    Row row = sheet.createRow(i + 1);

                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(column.name);
                    cell0.setCellStyle(cellStyle);

                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(column.type);
                    cell1.setCellStyle(cellStyle);

                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(column.length != null ? column.length : "");
                    cell2.setCellStyle(cellStyle);

                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(column.constraint != null ? column.constraint : "");
                    cell3.setCellStyle(cellStyle);

                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(column.comment != null ? column.comment : "");
                    cell4.setCellStyle(cellStyle);
                }

                sheet.setColumnWidth(0, 3500);
                sheet.setColumnWidth(1, 2500);
                sheet.setColumnWidth(2, 2000);
                sheet.setColumnWidth(3, 4500);
                sheet.setColumnWidth(4, 3500);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    static class TableInfo {
        String tableName;
        String comment;
        List<ColumnInfo> columns = new ArrayList<>();
    }

    static class ColumnInfo {
        String name;
        String type;
        String length;
        String constraint;
        String comment;
    }
}
