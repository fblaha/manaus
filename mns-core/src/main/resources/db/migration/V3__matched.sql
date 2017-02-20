ALTER TABLE SettledBet ADD matched DATETIME after placed;
ALTER TABLE SettledBet MODIFY placed DATETIME;