CREATE TABLE IF NOT EXISTS alert_type (
    name VARCHAR(40) NOT NULL,
    recipient VARCHAR(40),
    jurisdiction_id VARCHAR(4),
    template_name VARCHAR(255),
    max_frequency VARCHAR(80),
    constraint uq_alert_type__name_jurisdiction
		unique (name, jurisdiction_id)
);

insert into alert_type VALUES ('inactive-user-alert','qa-alert',null,'inactive_user_alert','365 days');
insert into alert_type VALUES ('se-numbers-decreased-alert','se-alert',null,'se-numbers-decreased_alert','1 day');
insert into alert_type VALUES ('se-numbers-increased-alert','se-alert',null,'se-numbers-increased_alert','1 day');
insert into alert_type VALUES ('user-logged-in-alert','cir-alert',null,'user-logged-in-alert','1 day');
insert into alert_type VALUES ('trapped-balance-alert','cft-alert',null,'trapped-balance-alert','365 days');



