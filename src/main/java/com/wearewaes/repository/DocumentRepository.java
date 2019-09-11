package com.wearewaes.repository;

import com.wearewaes.entities.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<Request, Long> {

}
