package com.epam.healenium.data;

import java.util.ArrayList;
import java.util.List;

public class LocatorInfo {
    private String reportName = "Healing Report";
    private String endTime;
    private List<Entry> elementsInfo = new ArrayList<>();

    public LocatorInfo() {}

    public String getReportName() {
        return reportName;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<Entry> getElementsInfo() {
        return elementsInfo;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setElementsInfo(List<Entry> elementsInfo) {
        this.elementsInfo = elementsInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocatorInfo)) return false;
        LocatorInfo other = (LocatorInfo) o;
        if (!other.canEqual(this)) return false;
        if (reportName != null ? !reportName.equals(other.reportName) : other.reportName != null) return false;
        if (endTime != null ? !endTime.equals(other.endTime) : other.endTime != null) return false;
        return elementsInfo != null ? elementsInfo.equals(other.elementsInfo) : other.elementsInfo == null;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LocatorInfo;
    }

    @Override
    public int hashCode() {
        int result = reportName != null ? reportName.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (elementsInfo != null ? elementsInfo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocatorInfo(reportName=" + reportName + ", endTime=" + endTime + ", elementsInfo=" + elementsInfo + ")";
    }

    public static class PageAsClassEntry extends Entry {
        private String declaringClass;
        private String methodName;
        private Integer lineNumber;
        private String fileName;

        public PageAsClassEntry() {}

        public String getDeclaringClass() {
            return declaringClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

        public String getFileName() {
            return fileName;
        }

        public void setDeclaringClass(String declaringClass) {
            this.declaringClass = declaringClass;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public void setLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return "LocatorInfo.PageAsClassEntry(declaringClass=" + declaringClass + ", methodName=" + methodName +
                    ", lineNumber=" + lineNumber + ", fileName=" + fileName + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PageAsClassEntry)) return false;
            if (!super.equals(o)) return false;
            PageAsClassEntry other = (PageAsClassEntry) o;
            if (!other.canEqual(this)) return false;
            if (declaringClass != null ? !declaringClass.equals(other.declaringClass) : other.declaringClass != null)
                return false;
            if (methodName != null ? !methodName.equals(other.methodName) : other.methodName != null) return false;
            if (lineNumber != null ? !lineNumber.equals(other.lineNumber) : other.lineNumber != null) return false;
            return fileName != null ? fileName.equals(other.fileName) : other.fileName == null;
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof PageAsClassEntry;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (declaringClass != null ? declaringClass.hashCode() : 0);
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            result = 31 * result + (lineNumber != null ? lineNumber.hashCode() : 0);
            result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
            return result;
        }
    }

    public static class SimplePageEntry extends Entry {
        private String pageName;

        public SimplePageEntry() {}

        public String getPageName() {
            return pageName;
        }

        public void setPageName(String pageName) {
            this.pageName = pageName;
        }

        @Override
        public String toString() {
            return "LocatorInfo.SimplePageEntry(pageName=" + pageName + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimplePageEntry)) return false;
            if (!super.equals(o)) return false;
            SimplePageEntry other = (SimplePageEntry) o;
            if (!other.canEqual(this)) return false;
            return pageName != null ? pageName.equals(other.pageName) : other.pageName == null;
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof SimplePageEntry;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (pageName != null ? pageName.hashCode() : 0);
            return result;
        }
    }

    public static class Entry {
        private String failedLocatorValue;
        private String failedLocatorType;
        private String healedLocatorValue;
        private String healedLocatorType;
        private String screenShotPath;

        public Entry() {}

        public String getFailedLocatorValue() {
            return failedLocatorValue;
        }

        public String getFailedLocatorType() {
            return failedLocatorType;
        }

        public String getHealedLocatorValue() {
            return healedLocatorValue;
        }

        public String getHealedLocatorType() {
            return healedLocatorType;
        }

        public String getScreenShotPath() {
            return screenShotPath;
        }

        public void setFailedLocatorValue(String failedLocatorValue) {
            this.failedLocatorValue = failedLocatorValue;
        }

        public void setFailedLocatorType(String failedLocatorType) {
            this.failedLocatorType = failedLocatorType;
        }

        public void setHealedLocatorValue(String healedLocatorValue) {
            this.healedLocatorValue = healedLocatorValue;
        }

        public void setHealedLocatorType(String healedLocatorType) {
            this.healedLocatorType = healedLocatorType;
        }

        public void setScreenShotPath(String screenShotPath) {
            this.screenShotPath = screenShotPath;
        }

        @Override
        public String toString() {
            return "LocatorInfo.Entry(failedLocatorValue=" + failedLocatorValue + ", failedLocatorType=" + failedLocatorType +
                    ", healedLocatorValue=" + healedLocatorValue + ", healedLocatorType=" + healedLocatorType +
                    ", screenShotPath=" + screenShotPath + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;
            Entry other = (Entry) o;
            if (!other.canEqual(this)) return false;
            if (failedLocatorValue != null ? !failedLocatorValue.equals(other.failedLocatorValue) : other.failedLocatorValue != null)
                return false;
            if (failedLocatorType != null ? !failedLocatorType.equals(other.failedLocatorType) : other.failedLocatorType != null)
                return false;
            if (healedLocatorValue != null ? !healedLocatorValue.equals(other.healedLocatorValue) : other.healedLocatorValue != null)
                return false;
            if (healedLocatorType != null ? !healedLocatorType.equals(other.healedLocatorType) : other.healedLocatorType != null)
                return false;
            return screenShotPath != null ? screenShotPath.equals(other.screenShotPath) : other.screenShotPath == null;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Entry;
        }

        @Override
        public int hashCode() {
            int result = failedLocatorValue != null ? failedLocatorValue.hashCode() : 0;
            result = 31 * result + (failedLocatorType != null ? failedLocatorType.hashCode() : 0);
            result = 31 * result + (healedLocatorValue != null ? healedLocatorValue.hashCode() : 0);
            result = 31 * result + (healedLocatorType != null ? healedLocatorType.hashCode() : 0);
            result = 31 * result + (screenShotPath != null ? screenShotPath.hashCode() : 0);
            return result;
        }
    }
}