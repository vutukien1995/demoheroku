package com.kien.demoheroku.dto;

public class ParagraphDTO {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Content : " + this.content;
    }
}
