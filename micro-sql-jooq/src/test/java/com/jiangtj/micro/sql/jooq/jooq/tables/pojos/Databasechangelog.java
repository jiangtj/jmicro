/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.sql.jooq.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public record Databasechangelog(
    String id,
    String author,
    String filename,
    LocalDateTime dateexecuted,
    Integer orderexecuted,
    String exectype,
    String md5sum,
    String description,
    String comments,
    String tag,
    String liquibase,
    String contexts,
    String labels,
    String deploymentId
) implements Serializable {

    private static final long serialVersionUID = 1L;


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Databasechangelog other = (Databasechangelog) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.author == null) {
            if (other.author != null)
                return false;
        }
        else if (!this.author.equals(other.author))
            return false;
        if (this.filename == null) {
            if (other.filename != null)
                return false;
        }
        else if (!this.filename.equals(other.filename))
            return false;
        if (this.dateexecuted == null) {
            if (other.dateexecuted != null)
                return false;
        }
        else if (!this.dateexecuted.equals(other.dateexecuted))
            return false;
        if (this.orderexecuted == null) {
            if (other.orderexecuted != null)
                return false;
        }
        else if (!this.orderexecuted.equals(other.orderexecuted))
            return false;
        if (this.exectype == null) {
            if (other.exectype != null)
                return false;
        }
        else if (!this.exectype.equals(other.exectype))
            return false;
        if (this.md5sum == null) {
            if (other.md5sum != null)
                return false;
        }
        else if (!this.md5sum.equals(other.md5sum))
            return false;
        if (this.description == null) {
            if (other.description != null)
                return false;
        }
        else if (!this.description.equals(other.description))
            return false;
        if (this.comments == null) {
            if (other.comments != null)
                return false;
        }
        else if (!this.comments.equals(other.comments))
            return false;
        if (this.tag == null) {
            if (other.tag != null)
                return false;
        }
        else if (!this.tag.equals(other.tag))
            return false;
        if (this.liquibase == null) {
            if (other.liquibase != null)
                return false;
        }
        else if (!this.liquibase.equals(other.liquibase))
            return false;
        if (this.contexts == null) {
            if (other.contexts != null)
                return false;
        }
        else if (!this.contexts.equals(other.contexts))
            return false;
        if (this.labels == null) {
            if (other.labels != null)
                return false;
        }
        else if (!this.labels.equals(other.labels))
            return false;
        if (this.deploymentId == null) {
            if (other.deploymentId != null)
                return false;
        }
        else if (!this.deploymentId.equals(other.deploymentId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.author == null) ? 0 : this.author.hashCode());
        result = prime * result + ((this.filename == null) ? 0 : this.filename.hashCode());
        result = prime * result + ((this.dateexecuted == null) ? 0 : this.dateexecuted.hashCode());
        result = prime * result + ((this.orderexecuted == null) ? 0 : this.orderexecuted.hashCode());
        result = prime * result + ((this.exectype == null) ? 0 : this.exectype.hashCode());
        result = prime * result + ((this.md5sum == null) ? 0 : this.md5sum.hashCode());
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.comments == null) ? 0 : this.comments.hashCode());
        result = prime * result + ((this.tag == null) ? 0 : this.tag.hashCode());
        result = prime * result + ((this.liquibase == null) ? 0 : this.liquibase.hashCode());
        result = prime * result + ((this.contexts == null) ? 0 : this.contexts.hashCode());
        result = prime * result + ((this.labels == null) ? 0 : this.labels.hashCode());
        result = prime * result + ((this.deploymentId == null) ? 0 : this.deploymentId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Databasechangelog (");

        sb.append(id);
        sb.append(", ").append(author);
        sb.append(", ").append(filename);
        sb.append(", ").append(dateexecuted);
        sb.append(", ").append(orderexecuted);
        sb.append(", ").append(exectype);
        sb.append(", ").append(md5sum);
        sb.append(", ").append(description);
        sb.append(", ").append(comments);
        sb.append(", ").append(tag);
        sb.append(", ").append(liquibase);
        sb.append(", ").append(contexts);
        sb.append(", ").append(labels);
        sb.append(", ").append(deploymentId);

        sb.append(")");
        return sb.toString();
    }
}
