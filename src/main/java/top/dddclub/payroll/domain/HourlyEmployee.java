package top.dddclub.payroll.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class HourlyEmployee {
    private static final double OVERTIME_FACTOR = 1.5;
    private List<TimeCard> timeCards;
    private Money salaryOfHour;

    public HourlyEmployee(List<TimeCard> timeCards, Money salaryOfHour) {
        this.timeCards = timeCards;
        this.salaryOfHour = salaryOfHour;
    }

    public Payroll payroll() {
        int regularHours = timeCards.stream()
                .map(TimeCard::getRegularWorkHours)
                .reduce(0, (hours, total) -> hours + total);

        int overtimeHours = timeCards.stream()
                .filter(TimeCard::isOvertime)
                .map(TimeCard::getOvertimeWorkHours)
                .reduce(0, (hours, total) -> hours + total);

        Money regularSalary = salaryOfHour.multiply(regularHours);
        Money overtimeSalary = salaryOfHour.multiply(OVERTIME_FACTOR).multiply(overtimeHours);
        Money totalSalary = regularSalary.add(overtimeSalary);

        return new Payroll(
                settlementPeriod().beginDate,
                settlementPeriod().endDate,
                totalSalary);
    }

    private Period settlementPeriod() {
        Collections.sort(timeCards);

        LocalDate beginDate = timeCards.get(0).workDay();
        LocalDate endDate = timeCards.get(timeCards.size() - 1).workDay();
        return new Period(beginDate, endDate);
    }

    private class Period {
        private LocalDate beginDate;
        private LocalDate endDate;

        Period(LocalDate beginDate, LocalDate endDate) {
            this.beginDate = beginDate;
            this.endDate = endDate;
        }
    }
}
