package com.example.demo.data.struc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a sample entity highlighting LOMBOK's capabilities.
 * 
 * @author amit.30.kumar
 */
@Entity
@Table(name = "users")
@Slf4j
@Data
@NoArgsConstructor // For JAVAX Persistence
@Builder
@AllArgsConstructor // For Builder
//@EqualsAndHashCode(of = { "lastName" })
//@ToString // (exclude = { "age" })
//@EntityListeners(AuditingEntityListener.class) // This is audit listener from spring data jpa - only works with @EnableJpaAuditing
@SuppressWarnings("serial")
public class User implements Serializable {

	@Id
	@Setter(AccessLevel.PROTECTED)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 25)
	private @NonNull String firstName;
	private @NonNull String lastName;
	private @Min(18) int age;

	public String getName() {
		log.debug("lombok does provide a log handles");
		return this.lastName + ", " + this.firstName;
	}
	
	/* Works with Audit - move it to @MappedSuperClass
	@Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;
 
    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;
    
    // To set these 2 values in a custom manner other than Spring Ctx Principal 
    // Use @EnableJpaAuditing(auditorAwareRef="customAuditNameResolver")
    // viz. public class AuditorAwareImpl implements AuditorAware<String> {
    //		@Override
    //		public String getCurrentAuditor() {
    //    		// your custom logic
    //		}
	// }
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
 
    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;
    */
}

// @SneakyThrows
// @Cleanup("method-to-call") - alternative to try-with-resources