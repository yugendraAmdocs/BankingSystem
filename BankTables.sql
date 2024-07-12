--branch table-------------------------------------------------------
DROP TABLE IF EXISTS  branches;

CREATE TABLE  branches  (
   Branch_id  number  NOT NULL,
   Branch_Name  varchar(45) NOT NULL,
   Street_Address  varchar(50) NOT NULL,
   City  varchar(25) NOT NULL,
   State  char(2) NOT NULL,
   Zipcode  number NOT NULL,
   Phone_Number  varchar(12) NOT NULL,
  PRIMARY KEY ( Branch_id )
);
--
alter table branches drop column state;
alter table branches add state varchar(20) NOT NULL;

CREATE SEQUENCE autoInc
START WITH 101
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER branch_bir
BEFORE INSERT ON branches
FOR EACH ROW
BEGIN
  :new.branch_id := autoInc.NEXTVAL;
END;

---loan type table -------------------------------------------------------------

CREATE TABLE loan_type(
type_id number NOT NULL PRIMARY KEY,
loan varchar(10) NOT NULL,
interest number NOT NULL
);

CREATE OR REPLACE TRIGGER loan_type_autoInc
BEFORE INSERT ON loan_type
FOR EACH ROW
BEGIN
  :new.type_id := autoInc.NEXTVAL;
END;

select * from employees;
select * from customers;
-----account type table---------------------------------------------------

CREATE TABLE account_type(
type_id number NOT NULL PRIMARY KEY,
account varchar(20) NOT NULL
);

CREATE OR REPLACE TRIGGER account_type_autoInc
BEFORE INSERT ON account_type
FOR EACH ROW
BEGIN
  :new.type_id := autoInc.NEXTVAL;
END;

----customers table--------------

CREATE TABLE customers (
  Customer_id number NOT NULL PRIMARY KEY,
  First_Name varchar(45) NOT NULL,
  Last_Name varchar(45) NOT NULL,
  Date_of_Birth date NOT NULL,
  Street_Address varchar(50) NOT NULL,
  City varchar(25) NOT NULL,
  State char(25) NOT NULL,
  Zipcode number NOT NULL,
  Email varchar(45) NOT NULL,
  Sex varchar(6) NOT NULL
);


CREATE OR REPLACE TRIGGER customers_autoInc
BEFORE INSERT ON customers
FOR EACH ROW
BEGIN
  :new.Customer_id := autoInc.NEXTVAL;
END;

-------------accounts------
CREATE TABLE accounts(
   Account_id  NUMBER NOT NULL PRIMARY KEY,
   Account_Balance  NUMBER NOT NULL,
   Branch_id  NUMBER,
   Date_Opened  DATE NOT NULL,
   Account_type_id  NUMBER,
   Customer_id NUMBER NOT NULL,
   CONSTRAINT fk_Accounts_Bank_details1 FOREIGN KEY (Branch_id) REFERENCES branches (Branch_id) ON DELETE SET NULL,
   CONSTRAINT fk_Accounts_account_type1 FOREIGN KEY (Account_type_id) REFERENCES account_type (type_id) ON DELETE SET NULL,
   CONSTRAINT fk_Customers_id FOREIGN KEY (Customer_id) REFERENCES Customers (Customer_id) ON DELETE SET NULL
);


alter table customers add constraints customer_id null;


CREATE OR REPLACE TRIGGER accounts_autoInc
BEFORE INSERT ON accounts
FOR EACH ROW
BEGIN
  :new.Account_id := autoInc.NEXTVAL;
END;

----------employees-----------------------------
create table departments(
dept_id number NOT NULL PRIMARY KEY,
dept_name varchar(10) NOT NULL
);

CREATE OR REPLACE TRIGGER dept_autoInc
BEFORE INSERT ON departments
FOR EACH ROW
BEGIN
  :new.dept_id := autoInc.NEXTVAL;
END;

------employees table--------------------------
DROP table employees;

CREATE TABLE employees (
    employee_id NUMBER NOT NULL PRIMARY KEY,
    first_name VARCHAR2(30) NOT NULL,
    last_name VARCHAR2(30) NOT NULL,
    DOB DATE NOT NULL,
    street_address VARCHAR2(100) NOT NULL,
    city VARCHAR2(20) NOT NULL,
    state VARCHAR2(20) NOT NULL,
    Zipcode NUMBER NOT NULL,
    sex VARCHAR2(6) NOT NULL,
    branch_id NUMBER,
    dept_id NUMBER,
    FOREIGN KEY (branch_id) REFERENCES branches (branch_id) ON DELETE SET NULL,
    FOREIGN KEY (dept_id) REFERENCES departments (dept_id) ON DELETE SET NULL
);


CREATE OR REPLACE TRIGGER employee_autoInc
BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
  :new.employee_id := autoInc.NEXTVAL;
END;

----------credit cards-------------------------------------
create table credit_cards(
cc_number number not null primary key,
max_limit number not null , 
customer_id number ,
due_amount number ,
balance_amount number,
expiry_date date not null,
FOREIGN KEY (customer_id) references customers (customer_id) on delete set null
);

CREATE OR REPLACE TRIGGER cc_autoInc
BEFORE INSERT ON credit_cards
FOR EACH ROW
BEGIN
  :new.cc_number := autoInc.NEXTVAL;
END;
----------cc_transcations------------------------------

create table cc_transactions(
cc_transaction_id number not null primary key,
cc_number number not null ,
amount number not null ,
foreign key (cc_number) references credit_cards(cc_number) on delete set null
);

CREATE OR REPLACE TRIGGER cc_transactions_autoInc
BEFORE INSERT ON cc_transactions
FOR EACH ROW
BEGIN
  :new.cc_transaction_id := autoInc.NEXTVAL;
END;

--------------------banking transactions----
create table Banking_transactions (
transaction_id number not null primary key,
amount number not null,
sender_id number not null,
reciever_id number not null,
transaction_date date not null,
foreign key (sender_id) references customers (customer_id) on delete set null,
foreign key (reciever_id) references customers (customer_id) on delete set null
);

CREATE OR REPLACE TRIGGER transactions_autoInc
BEFORE INSERT ON Banking_transactions
FOR EACH ROW
BEGIN
  :new.transaction_id := autoInc.NEXTVAL;
END;
-----------------------------loan------------------------------
create table Loans (
loan_id number not null primary key,
customer_id number not null ,
interest number not null,
type_id number not null,
tenure number not null,
foreign key (customer_id) references customers (customer_id) on delete set null,
foreign key (type_id) references loan_type (type_id) on delete set null
);

alter table Loans add monthly_installment number not null;
alter table Loans add total_payble number not null;

CREATE OR REPLACE TRIGGER loans_autoInc
BEFORE INSERT ON Loans
FOR EACH ROW
BEGIN
  :new.loan_id := autoInc.NEXTVAL;
END;

------------------------requests_loan---------------
create table loan_requests(
request_id number not null primary key,
customer_id number not null,
income number not null,
tenure number not null,
loan_type_id number not null,
amount number not null,
foreign key (customer_id) references customers (customer_id) on delete set null,
foreign key (loan_type_id) references loan_type (type_id) on delete set null
);

alter table loan_requests add status varchar(10);

CREATE OR REPLACE TRIGGER loan_request_autoInc
BEFORE INSERT ON loan_requests
FOR EACH ROW
BEGIN
  :new.request_id := autoInc.NEXTVAL;
END;

---------------------requests_credit_card----------
create table cc_requests(
request_id number not null primary key,
customer_id number not null ,
status varchar(10) not null ,
foreign key (customer_id) references customers (customer_id) on delete set null
);


CREATE OR REPLACE TRIGGER cc_request_autoInc
BEFORE INSERT ON cc_requests
FOR EACH ROW
BEGIN
  :new.request_id := autoInc.NEXTVAL;
END;