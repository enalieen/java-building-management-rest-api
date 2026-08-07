CREATE OR REPLACE TABLE `Customer` (
	`id` UUID NOT NULL UNIQUE,
	`firstName` TEXT(65535),
	`lastName` TEXT(65535),
	`Gender` ENUM('D', 'M', 'U', 'W'),
	`birthDate` DATE,
	PRIMARY KEY(`id`)
);

CREATE OR REPLACE TABLE `Reading` (
	`id` UUID NOT NULL UNIQUE,
	`comment` TEXT(65535),
	`dateOfReading` DATE,
	`meterCount` DOUBLE,
	`meterId` TEXT(65535),
	`customer` UUID,
	`substitute` BOOLEAN,
	`kindOfMeter` ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT'),
	PRIMARY KEY(`id`)
);

ALTER TABLE `Reading`
ADD FOREIGN KEY(`customer`) REFERENCES `Customer`(`id`)
ON UPDATE NO ACTION ON DELETE SET NULL;