package com.yeokm1.nussocprintandroid.print_activities.printing;

public class PrintingProgressItem {

    private String title;
    private String subtitle;
    private boolean progressIndeterminate;
    private float progressValue;

    private boolean showDoneIcon;
    private boolean showErrorIcon;

    private boolean isProgressBarActive;

    public PrintingProgressItem(String title, String subtitle, boolean isCompleted, boolean showErrorIcon, boolean isProgressBarActive) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.progressIndeterminate = true;
        this.showDoneIcon = isCompleted;
        this.showErrorIcon = showErrorIcon;
        this.isProgressBarActive = isProgressBarActive;

        this.progressValue = 0;
    }

    public PrintingProgressItem(String title, String subtitle, float progressValue, boolean showDoneIcon, boolean showErrorIcon, boolean isProgressBarActive) {
        this(title, subtitle, showDoneIcon, showErrorIcon, isProgressBarActive);
        this.progressValue = progressValue;
        this.progressIndeterminate = false;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isProgressIndeterminate() {
        return progressIndeterminate;
    }

    public float getProgressValue() {
        return progressValue;
    }

    public boolean isShowDoneIcon() {
        return showDoneIcon;
    }

    public boolean isShowErrorIcon() {
        return showErrorIcon;
    }

    public boolean isProgressBarActive() {
        return isProgressBarActive;
    }

}
