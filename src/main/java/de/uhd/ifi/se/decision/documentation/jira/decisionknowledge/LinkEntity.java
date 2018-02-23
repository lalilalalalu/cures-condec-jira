package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * @author Ewald Rode
 * @description Model class for links between decision components
 */
@Table("LINK")
public interface LinkEntity extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	public long getID();

	public String getType();

	public void setType(String type);

	public long getIngoingId();

	public void setIngoingId(long ingoingId);

	public long getOutgoingId();

	public void setOutgoingId(long outgoingId);
}
