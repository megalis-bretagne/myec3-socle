package org.myec3.socle.core.domain.model;


import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

@Entity
@Synchronized
public class ExportCSV  implements Serializable, Cloneable, PE{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;


    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private File exportFile;

    public ExportCSV() {
        super();
        creationDate=new Date(System.currentTimeMillis());
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Technical id of the resource
     *
     * @return the id for this resource
     */
    public Long getId() {
        return id;
    }


}
