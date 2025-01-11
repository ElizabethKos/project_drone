package org.example.drone.persistence.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Drone {
    Integer cost;
    String name;
    String description;


    public Drone(Integer cost, String name, String Desc) {
        this.name = name;
        this.cost = cost;
        this.description = Desc;
    }


    @Override
    public String toString() {
        return "Parachute{" +
                "cost=" + cost +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
