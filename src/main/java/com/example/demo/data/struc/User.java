package com.example.demo.data.struc;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/*
 * This is a sample entity highlighting LOMBOK's capabilities. 
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString // (exclude = { "age" })
@EqualsAndHashCode(of = { "lastname" })
@Builder
@Slf4j
@SuppressWarnings("serial")
//@EntityListeners(AuditingEntityListener.class) // This is audit listener from spring data jpa - only works with @EnableJpaAuditing
public class User implements Serializable {

	@Id
	@Setter(AccessLevel.PROTECTED)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 25)
	private @NonNull String firstname;
	private @NonNull String lastname;
	private @Min(18) int age;

	public String getName() {
		log.debug("lombok does provide a log handles");
		return this.lastname + ", " + this.firstname;
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