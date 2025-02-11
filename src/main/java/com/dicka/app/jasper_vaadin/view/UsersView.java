package com.dicka.app.jasper_vaadin.view;

import com.dicka.app.jasper_vaadin.entity.Users;
import com.dicka.app.jasper_vaadin.service.UsersService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Route("")
public class UsersView extends VerticalLayout {

    private final UsersService usersService;
    private final Grid<Users> usersGrid = new Grid<>(Users.class);

    private final TextField idField = new TextField("ID");
    private final TextField fullNameField = new TextField("FULL NAME");
    private final TextField emailField = new TextField("EMAIL");
    private final TextField phoneNumberField = new TextField("PHONE NUMBER");
    private final TextField addressDetailField = new TextField("ADDRESS DETAIL");
    private final DatePicker dobField = new DatePicker("DOB");
    private final Button saveButton = new Button("Save");
    private final Button deleteButton = new Button("Delete");
    private final Button generateReport = new Button("Generate Report");

    @Autowired
    public UsersView(UsersService usersService){
        this.usersService = usersService;
        updateGrid();
        saveButton.addClickListener(event -> saveOrUpdate());
        deleteButton.addClickListener(event -> deleteData());
        usersGrid.asSingleSelect().addValueChangeListener(event -> populatedDateFromSelected(event.getValue()));

        idField.setSizeFull();
        idField.setEnabled(false);
        fullNameField.setSizeFull();
        emailField.setSizeFull();
        phoneNumberField.setSizeFull();
        addressDetailField.setSizeFull();
        dobField.setSizeFull();

        HorizontalLayout horizontalLayoutButton = new HorizontalLayout(saveButton, deleteButton);

        VerticalLayout formVerticalLayout = new VerticalLayout(
                idField,
                fullNameField,
                phoneNumberField,
                emailField,
                addressDetailField,
                dobField,
                horizontalLayoutButton
        );
        formVerticalLayout.setWidth("30%");

        usersGrid.setSizeFull();
        generateReport.addClickListener(event -> downloadReport());

        VerticalLayout  gridVerticalLayout = new VerticalLayout(usersGrid, generateReport);
        HorizontalLayout mainLayout = new HorizontalLayout(formVerticalLayout, gridVerticalLayout);

        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        add(mainLayout);
    }

    private void saveOrUpdate(){
        Long id = !idField.isEmpty() ? Long.parseLong(idField.getValue()) : null;
        String fullName = fullNameField.getValue();
        String email = emailField.getValue();
        String phoneNumber = phoneNumberField.getValue();
        String addressDetail = addressDetailField.getValue();
        LocalDate dobValue = dobField.getValue();
        Date dob = (!Objects.isNull(dobValue)) ?
                Date.from(dobValue.atStartOfDay(ZoneId.systemDefault()).toInstant()) :
                null;

        /** create validation **/
        List<String> errorMessages = new ArrayList<>();
        if (fullName.isEmpty()){
            errorMessages.add("Full name is required !");
        }
        if (email.isEmpty()){
            errorMessages.add("Email is required !");
        }
        if (phoneNumber.isEmpty()){
            errorMessages.add("Phone number is required !");
        }
        if (addressDetail.isEmpty()){
            errorMessages.add("Address detail is required !");
        }

        if (Objects.isNull(dob)){
            errorMessages.add("DOB is required !");
        }

        if (!errorMessages.isEmpty()){
            Notification.show(String.join("\n",errorMessages));
            return;
        }

        /** check update or delete **/
        if (Objects.isNull(id)){
            log.info("process save data..");
            Users users = Users.builder()
                    .fullName(fullName).addressDetail(addressDetail).email(email)
                    .phoneNumber(phoneNumber)
                    .dob(dob)
                    .build();
            usersService.handleSaveOrUpdate(users);

            if (!Objects.isNull(users)){
                Notification.show("Users is successfully created !");
                updateGrid();
            }
        }else{
            log.info("process update data..");
            Users currentUsers = usersService.findUsersById(id);
            currentUsers.setId(id);
            currentUsers.setFullName(fullName);
            currentUsers.setEmail(email);
            currentUsers.setPhoneNumber(phoneNumber);
            currentUsers.setDob(dob);
            usersService.handleSaveOrUpdate(currentUsers);

            Notification.show("Users is successfully updated !");
            updateGrid();
        }

        /** clear form **/
        clearForm();
    }

    private void updateGrid(){
        usersGrid.setItems(usersService.findAllUsers());
    }

    private void clearForm(){
        idField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneNumberField.clear();
        addressDetailField.clear();
        dobField.clear();
    }

    private void populatedDateFromSelected(Users users){
        if (!Objects.isNull(users)){
            Notification.show("Data is already populated !");
            idField.setValue(!Objects.isNull(users.getId()) ? String.valueOf(users.getId()) : "");
            fullNameField.setValue(!Objects.isNull(users.getFullName()) ? users.getFullName() : "");
            emailField.setValue(!Objects.isNull(users.getEmail()) ? users.getEmail() : "");
            phoneNumberField.setValue(!Objects.isNull(users.getPhoneNumber()) ? users.getPhoneNumber() : "");
            addressDetailField.setValue(!Objects.isNull(users.getAddressDetail()) ? users.getAddressDetail() : "");
            dobField.setValue(!Objects.isNull(users.getDob()) ?
                    users.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);
        }
    }

    private void deleteData(){
        Long id = !idField.isEmpty() ? Long.parseLong(idField.getValue()) : null;
        if (Objects.isNull(id)){
            Notification.show("Please selected data !");
            return;
        }
        usersService.deleteUserById(id);
        updateGrid();
        clearForm();
        Notification.show("Delete data successfully !");
    }

    private void downloadReport() {
        //call api generate report pdf jasper
        String reportUrl = "http://localhost:8881/api/reports";
        UI.getCurrent().getPage().executeJs(
                "const link = document.createElement('a');" +
                        "link.href = $0;" +
                        "link.download = 'report.pdf';" +
                        "document.body.appendChild(link);" +
                        "link.click();" +
                        "document.body.removeChild(link);",
                reportUrl
        );
    }
}
