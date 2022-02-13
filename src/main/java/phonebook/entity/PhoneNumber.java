package phonebook.entity;

import javax.persistence.*;

@Entity
public class PhoneNumber {
    public static final int MAX_VALUE_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = MAX_VALUE_LENGTH)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", nullable = false)
    private Person owner;

    public PhoneNumber(String value) {
        this.value = value;
    }

    public PhoneNumber() {}

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return value;
    }
}
