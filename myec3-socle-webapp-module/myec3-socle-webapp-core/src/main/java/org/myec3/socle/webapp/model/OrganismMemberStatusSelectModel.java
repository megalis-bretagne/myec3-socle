package org.myec3.socle.webapp.model;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.myec3.socle.core.domain.model.OrganismMemberStatus;

import java.util.ArrayList;
import java.util.List;

public class OrganismMemberStatusSelectModel extends AbstractSelectModel {

    private List<OrganismMemberStatus> listStatus;

    public OrganismMemberStatusSelectModel(List<OrganismMemberStatus> listStatus) {
        this.listStatus = listStatus;
    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = new ArrayList<OptionModel>();
        for (OrganismMemberStatus status : listStatus) {
            options.add(new OptionModelImpl(status.getName(), status.getKey()));
        }
        return options;
    }
    
}
