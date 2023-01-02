# dinekeeper

A lightweight bookkeeping app for restaurant management.

## Description
Java Swing application built using the **MVC pattern** that enables table management, reservation management, and financial bookkeeping that supports scheduling. 

## App details
The app features three separate views, all with dynamic tables with adjustable columns by right clicking the column header.


Restaurant tab- create and manage tables and their availability. Tables can be removed by ID, and availability can be changed by 
checking/unchecking the box in the table. 

Calendar tab- make and manage upcoming reservations. Making a reservation automatically allocates it to an optimal table (with minimum
seats >= number of guests). Reservations can be removed by selecting it and pressing remove. All cells (other than name or table) are
directly mutable by double clicking and overwritting the cell. Time should be inputted in (HH:mm MM/dd/yyyy) format in the cell to change.
To indicate that a reservation is complete, press service and the reservation will be moved to the _Ledger_.
 - Save button saves data in both calendar and ledger tabs.

Ledger tab- showcases all previous reservations with their earnings. Calculate earnings within a timeframe by inputting start and
end dates (in MM/dd/yyyy format).

## Employed tech stack: 
Joda-Time 2.4, Java MVC architecture, Serialization, Swing GUI, TableColumnManager
