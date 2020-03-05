package com.aem.community.core.models.timeComponent;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Model(adaptables = Resource.class)
public class TimeComponent {

    @Inject
    @Optional
    //    @Named("dateNow")
    private String dateNow;

    @Inject
    @Optional
    //    @Named("timeNow")
    private String timeNow;

    @PostConstruct
    public void init() {
        dateNow = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        timeNow = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
    }

    public String getDateNow() {
        return dateNow;
    }

    public String getTimeNow() {
        return timeNow;
    }
}
