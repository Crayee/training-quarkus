package de.istec.training.example1;

import de.istec.training.example1.dto.GroupValuesRow;
import de.istec.training.example1.source.ExampleData;
import de.istec.training.example1.source.TimeValue;
import de.istec.training.example1.source.ValueGroup;
import de.istec.training.example1.util.Month;
import de.istec.training.example1.util.Period;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Example1Service {

    public Collection<GroupValuesRow> buildRows(Period period) {
        /*
            The logic should transform ExampleData.data() (simulates a select joining ValueGroup and TimeValue) in a List of GroupValuesRow.
            The UI should be enabled to display the data in a DataGrid with the following structure
            Group-Name | Value-Type | <Value-Month-1> | ... | <Value-Month-n>
            Group-1 | Reference | ...
            The UI expects the data sorted by Group-Name, Value-Type (Reference, Character). In the UI we want to highlight the cell
            if the value has changed in comparison to the previous month. We also want to be able to render referenced values in a different way
            than unreferenced values.
            TODO implement the logic without implicit loops
            Hints:
            * you can create as many classes, interfaces, methods as you want
            * you can mutate all classes, interfaces, records, ... but ExampleData
            * assume that a real world example has more than 2 value-types
            * try to be declarative not imperative
        */

        var valueCellFactory = new ValueCellFactory(transformData(period, ExampleData.data()), period);

        return ExampleData.groups()
                .sorted(Comparator.comparing(ValueGroup::name))
                .flatMap(group ->
                        Stream.of(ExampleData.TYPE_REFERENCE, ExampleData.TYPE_CHARACTER)
                                .map(type -> new GroupValuesRow(group.name(), type, valueCellFactory.createValueCells(group.id(), type)))
                )
                .toList();
    }

    private GroupValueDao transformData(Period period, Stream<Pair<ValueGroup, TimeValue>> data) {
        var resultMap = data
                .flatMap(pair -> periodOfTimeValue(pair.getValue1(), period).stream()
                        .map(month -> Pair.with(
                                new GroupTypeMonth(pair.getValue0().id(), pair.getValue1().type(), month),
                                pair.getValue1()
                        ))
                )
                .collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));

        return (key) -> Optional.ofNullable(resultMap.get(key));
    }

    private Period periodOfTimeValue(TimeValue timeValue, Period relevant) {
        return new Period(
                Month.of(timeValue.validFrom()).max(relevant.from().previous()),
                Month.of(timeValue.validTo()).min(relevant.until())
        );
    }
}
