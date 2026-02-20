delete from payments;
delete from rentals;
delete from cars;
delete from users;

INSERT IGNORE INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted)
VALUES (1, 'X5', 'BMW', 'SUV', 5, 50.00, 0),
       (2, 'Model S', 'Tesla', 'SEDAN', 3, 100.00, 0);

insert into rentals
(id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted, is_active)
VALUES (1, '2026-02-10', '2026-02-14', '2026-02-14', 1, 2, 0, 0),
       (2, '2026-02-18', '2026-02-20', null, 2, 2, 0, 1),
       (3, '2026-02-17', '2026-02-21', null, 1, 1, 0, 1),
       (4, '2026-02-05', '2026-02-09', '2026-02-09', 2, 2, 0, 0),
       (5, '2026-02-04', '2026-02-08', '2026-02-08', 2, 1, 0, 0);

