DELETE
FROM payments;
DELETE
FROM rentals;
DELETE
FROM users;
DELETE
from roles;
DELETE
FROM cars;

INSERT IGNORE INTO roles (id, role, is_deleted)
VALUES (1, 'CUSTOMER', 0),
       (2, 'MANAGER', 0);

INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
VALUES (1, 'user1@mail.com', 'Adsadsad', 'Bqwewqeqw', 'passdsadsa', 0),
       (2, 'user2@mail.com', 'Cdsadsad', 'Dedsadsadsa', 'passdsadsad', 0);

INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted)
VALUES (1, 'X5', 'BMW', 'SUV', 5, 50.00, 0),
       (2, 'Model S', 'Tesla', 'SEDAN', 3, 100.00, 0);

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_active, is_deleted)
VALUES (1, '2026-02-10', '2026-02-14', '2026-02-14', 1, 2, 0, 0),
       (2, '2026-02-18', '2026-02-20', null, 2, 2, 1, 0),
       (3, '2026-02-17', '2026-02-21', null, 1, 1, 1, 0),
       (4, '2026-02-05', '2026-02-09', '2026-02-09', 2, 2, 0, 0),
       (5, '2026-02-04', '2026-02-08', '2026-02-08', 2, 1, 0, 0);

insert into payments (id, rental_id, session_url, session_id, total, status, type, is_deleted)
VALUES (1, 1,
        'https://checkout.stripe.com/c/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0#fidkdWxOYHwnPyd1blpxYHZxWjA0S1N3YHV3fGZka3ZpYmZpYHV3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3',
        'cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', 2000, 'PAID', 'PAYMENT', 0),
       (2, 4,
        'https://checkout.stripe.com/c/pay/cs_test_B9876543210asdfghjklqwertyuiopzxcvbnm123#fidkdWxOYHwnPyd1blpxYHZxWjA0S1N3YHV3fGZka3ZpYmZpYHV3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3',
        'cs_test_B9876543210asdfghjklqwertyuiopzxcvbnm123', 4600, 'PAID', 'PAYMENT', 0);