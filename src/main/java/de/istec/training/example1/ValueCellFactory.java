package de.istec.training.example1;

import de.istec.training.example1.dto.ValueCell;
import de.istec.training.example1.util.Period;

import java.util.List;

public record ValueCellFactory(TimeValues timeValues, Period period) {

    public List<ValueCell> createRowCells(long groupId, String type) {
        return period.stream()
                .map(month -> new GroupTypeMonth(groupId, type, month))
                .map(this::createValueCell)
                .toList();
    }

    private ValueCell createValueCell(GroupTypeMonth groupTypeMonth) {
        var referenced = timeValues.isReferenced(groupTypeMonth);
        var changed = timeValues.hasChanged(groupTypeMonth);
        return timeValues.getValue(groupTypeMonth)
                .map(tv -> new ValueCell(tv.id(), tv.value(), referenced, changed))
                .orElse(ValueCell.empty(referenced, changed));
    }

}
