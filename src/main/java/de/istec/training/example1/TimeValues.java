package de.istec.training.example1;

import de.istec.training.example1.source.ExampleData;
import de.istec.training.example1.source.Reference;
import de.istec.training.example1.source.TimeValue;

import java.util.Objects;
import java.util.Optional;

@FunctionalInterface
public interface TimeValues {
    Optional<TimeValue> timeValue(GroupTypeMonth key);

    default Optional<TimeValue> getValue(GroupTypeMonth key) {
        return timeValue(key).or(() -> getReferencedValue(key));
    }

    default boolean isReferenced(GroupTypeMonth key) {
        return getReferencedValue(key).isPresent();
    }

    default boolean hasChanged(GroupTypeMonth key) {
        var value = getValue(key).map(TimeValue::value).orElse(null);
        var prevValue = getValue(new GroupTypeMonth(key.groupId(), key.type(), key.month().previous()))
                .map(TimeValue::value)
                .orElse(null);
        return !Objects.equals(value, prevValue);
    }

    private Optional<TimeValue> getReferencedValue(GroupTypeMonth key) {
        return timeValue(new GroupTypeMonth(key.groupId(), ExampleData.TYPE_REFERENCE, key.month()))
                .map(ref -> (Reference) ref.value())
                .flatMap(ref -> timeValue(new GroupTypeMonth(ref.groupId(), key.type(), key.month())));
    }
}
