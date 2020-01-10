package org.myec3.socle.webapp.model;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.myec3.socle.core.domain.model.CollegeCategorie;

import java.util.ArrayList;
import java.util.List;

public class CollegeCategorieSelectModel extends AbstractSelectModel  {
    
    private List<CollegeCategorie> colleges;
    
    public CollegeCategorieSelectModel(List<CollegeCategorie> colleges) {
        this.colleges = colleges;
    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = new ArrayList<OptionModel>();
        for (CollegeCategorie college : colleges) {
            options.add(new OptionModelImpl(college.getName(), college.getKey()));
        }
        return options;
    }
    
}
