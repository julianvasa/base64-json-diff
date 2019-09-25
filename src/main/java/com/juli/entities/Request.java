package com.juli.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * A Request is the object (type String) being sent through a post request
 */
public class Request {
    @Id
    private long id;

    @Column
    private String left;

    @Column
    private String right;

}
