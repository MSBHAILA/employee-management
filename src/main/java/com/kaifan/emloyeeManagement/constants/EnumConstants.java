package com.kaifan.emloyeeManagement.constants;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EnumConstants {

    public enum Status {
        ACTIVE, INACTIVE, ON_LEAVE, TERMINATED, PENDING, EXPIRED, COMPLETED
    }

    public enum LeaveStatus {
        SUBMITTED,
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    public enum ApprovalDecision {
        APPROVED,
        REJECTED
    }
//    public enum Position {
//        HEAD_OF_COMPUTER_DEPARTMENT("رئيس قسم الحاسبات"),
//        HEAD_OF_ACCOUNTING_DEPARTMENT("رئيس قسم الحسابات"),
//        HEAD_OF_FINANCE_DEPARTMENT("رئيس قسم المالية"),
//        HEAD_OF_HR_DEPARTMENT("رئيس قسم الموارد البشرية"),
//        PROGRAMMER("مبرمج"),
//        ACCOUNTANT("حسابات");
//        private String name;
//        Position(String name) {
//            this.name = name;
//        }
//        public String getName() {
//            return name;
//        }
//
//        @JsonCreator
//        public static Position fromArabicName(String arabicName) {
//            for (Position position : Position.values()) {
//                if (position.getName().equals(arabicName)) {
//                    return position;
//                }
//            }
//            throw new IllegalArgumentException("Invalid position: " + arabicName);
//        }
//    }
}
