package com.pplflw.prototype.repositories;

import com.pplflw.prototype.domains.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployersRepository extends JpaRepository<Employer, Long> {
}
