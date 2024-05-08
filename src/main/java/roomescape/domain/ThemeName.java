package roomescape.domain;

import java.util.Objects;
import roomescape.exceptions.MissingRequiredFieldException;

public record ThemeName(String name) {

    public ThemeName {
        validate(name);
    }

    private void validate(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new MissingRequiredFieldException("테마 이름은 필수 값입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThemeName name1 = (ThemeName) o;
        return Objects.equals(name, name1.name);
    }
}
