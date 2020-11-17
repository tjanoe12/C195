Program Title: C195 Scheduling Application
Purpose: To allow better management of appointments, customers, and time in general. Display information for users on customers, contacts, appointments etc.
Author: Taylor Daniel Janoe
Contact Info: tjanoe@wgu.edu

Application Version: 1.1
Date of build: 16 November 2020
Previous Version 1.0
Date of build: 12 November 2020

IDE: IntelliJ IDEA 2020.1.3 (Community Edition)
    Build #IC-201.8538.31, built on July 7, 2020
    Runtime version: 11.0.7+10-b765.64 x86_64
    VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
    macOS 10.15.6
    GC: ParNew, ConcurrentMarkSweep
    Memory: 1981M
    Cores: 8
    Registry: debugger.watches.in.variables=false
JAVA SE 11.0.8
JAVAFX SDK 11.0.2

INSTRUCTIONS
With valid login information access the main menu where you can view appointments, customers, a users section, and reports.
***All login attempts are logged with either success, failure and time of login verified against the database
Customers: view a list of customers and their information in a table view, you can add new customers, edit current customers once one is selected or delete the currently selected customer.
***Deleting a customer deletes all associated appointments***
--Adding a new customer: Ensure all information is set in each field, note the format for the date/time fields must be like the prompt text. If you wish to discard the update go back to the customer main view via the exit button. Customer ID's are preset and uneditable. The created by and last updated by text fields will prepopulate with the user who logged in but can be modified
--Editing a current customer: all information should pre-populate based on selection, all fields are editable besides the customer ID. The country selection list will modify the available divisions to that specific country.
--Deleting a current customer: Deletes current customer ALL appointments associated with that customer will be deleted as well.

Appointments: works similarly to the Customers section, on the edit appointment a drop down with a Contact Name will populate the Contact ID field.

Users: Initially brings up all appointments into a table view. Above the table are two radio buttons which filter by weekly or monthly appointments from today's current date.


REPORTS...
Customer Appointments: Shows a report of the number of customer appointments per month and the number of appointments based on type

Contact Schedules: From the dropdown, select a contact and view their assigned appointments in the tableview

Customer Schedules: From the dropdown, select a customer and view their assigned appointments in the tableview. Quickly be able to navigate and use the customers information specifically.
