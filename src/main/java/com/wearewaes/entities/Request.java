package com.wearewaes.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    /**
     * A Request is the object (type String) being sent through a post request
     */
    @Id
    private long id;

    @Column
    private String left;

    @Column
    private String right;

}
