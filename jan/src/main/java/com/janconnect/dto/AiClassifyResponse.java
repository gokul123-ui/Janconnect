package com.janconnect.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiClassifyResponse {
    private String categoryId;
    private String categoryName;
    private String departmentName;
    private String issueTitle;
    private String priority;
    private String description;
    private String language;
    private List<String> missingInformation;

    // Multilingual summaries
    private MultiLangText aiSummary;
    private MultiLangText requestedAction;
    private MultiLangText issueNameI18n;
    private MultiLangText departmentNameI18n;
    private MultiLangText categoryNameI18n;

    @Data
    public static class MultiLangText {
        private String en;
        private String ta;
        private String hi;
        private String kn;

        public MultiLangText(String en, String ta, String hi, String kn) {
            this.en = en;
            this.ta = ta;
            this.hi = hi;
            this.kn = kn;
        }
    }
}
