package org.librarymanagement.constant;

public enum BRStatusConstant {
    PENDING(0, "Đang chờ", "bg-warning"),
    COMPLETED(1, "Hoàn tất", "bg-success"),
    CANCELED(2, "Đã huỷ", "bg-danger"),
    OVERDUE(3, "Quá hạn", "bg-danger"),
    RETURNED(4,"Đã trả","bg_sucess");
    private final int value;
    private final String label;
    private final String cssClass;

    BRStatusConstant(int value, String label, String cssClass) {
        this.value = value;
        this.label = label;
        this.cssClass = cssClass;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }
    public String getCssClass() { return cssClass; }

    public static BRStatusConstant fromValue(int value) {
        for (BRStatusConstant status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
