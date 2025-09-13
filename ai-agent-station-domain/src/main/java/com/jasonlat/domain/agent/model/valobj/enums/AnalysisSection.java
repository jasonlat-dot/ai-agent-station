package com.jasonlat.domain.agent.model.valobj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AnalysisSection {
    ANALYSIS_STATUS("任务状态分析:", "analysis_status", "📋 "),
    ANALYSIS_HISTORY("执行历史评估:", "analysis_history", "📊 "),
    ANALYSIS_STRATEGY("下一步策略:", "analysis_strategy", "🎯 "),
    ANALYSIS_PROGRESS("完成度评估:", "analysis_progress", "📊 "),
    ANALYSIS_TASK_STATUS("任务状态:", "analysis_task_status", "");

    private String sectionMarker;
    private String sectionKey;
    private String logPrefix;

    // 根据标记查找对应的section
    public static AnalysisSection findByMarker(String line) {
        for (AnalysisSection section : values()) {
            if (line.contains(section.sectionMarker)) {
                return section;
            }
        }
        return null;
    }
}