
package fastcampus.spring.batch.part3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String age;
    private String address;

    public Person(int id, String name, String age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Person(String name, String age, String address) {
        this.id = 0;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public boolean isNotEmptyName() {
        return Objects.nonNull(this.name) && !name.isEmpty();
    }

    public Person unknownName() {
        this.name = "UNKNOWN";
        return this;
    }
}
