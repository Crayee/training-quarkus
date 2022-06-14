package de.istec.training.example1;

import de.istec.training.example1.dto.ValueCell;
import de.istec.training.example1.util.Period;

import java.util.List;

public record ValueCellFactory(TimeValues groupValueDao, Period period) {

    public List<ValueCell> createValueCells(long groupId, String type) {
        return period.stream()
                .map(month -> new GroupTypeMonth(groupId, type, month))
                .map(this::createValueCell)
                .toList();
    }

    private ValueCell createValueCell(GroupTypeMonth groupTypeMonth) {
        var isReferenced = groupValueDao.isReferenced(groupTypeMonth);
        var isChanged = groupValueDao.hasChanged(groupTypeMonth);
        return groupValueDao.getValue(groupTypeMonth)
                .map(tv -> new ValueCell(tv.id(), tv.value(), isReferenced, isChanged))
                .orElse(ValueCell.empty(isReferenced, isChanged));
    }

}
