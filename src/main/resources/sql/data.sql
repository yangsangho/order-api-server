insert into member
values ('01887837-426e-d5fb-b9af-4cbc3619f044', '양상호', '01012341234', now(), now()),
       ('0188786b-a80f-904c-5276-087998d7d930', '김영희', '01078787878', now(), now());

insert into product
values ('01887839-e8f1-ed2f-c023-e4f629ebd8cd', '선풍기', 65000, now(), now()),
       ('0188783a-b1c0-88fd-9e79-787ad03616bb', '충전기', 7000, now(), now()),
       ('0188783b-08dc-d8ac-c345-8a9e211afd5e', '이어폰', 30000, now(), now());

insert into orders
values ('01887846-a38a-48e1-017f-00dc5de4361a', '01887837-426e-d5fb-b9af-4cbc3619f044', 'COMPLETED', '양상호',
        '01012341234', '서울특별시', '문 앞에 두세요', now(), now()),
       ('0188784d-b42f-3f52-2783-b0f8ef2bb032', '01887837-426e-d5fb-b9af-4cbc3619f044', 'RECEIPTED', '김아무개',
        '01099999999', '강원도', null, now(), now());

insert into orders_product
values ('01887846-a38a-48e1-017f-00dc5de4361b', '01887846-a38a-48e1-017f-00dc5de4361a',
        '01887839-e8f1-ed2f-c023-e4f629ebd8cd', 1, now(), now()),
       ('01887846-a38a-48e1-017f-00dc5de4361c', '01887846-a38a-48e1-017f-00dc5de4361a',
        '0188783a-b1c0-88fd-9e79-787ad03616bb', 3, now(), now()),
       ('0188784e-78db-ed34-b32f-adf7c036b4d0', '0188784d-b42f-3f52-2783-b0f8ef2bb032',
        '0188783b-08dc-d8ac-c345-8a9e211afd5e', 10, now(), now());

insert into payment
values ('01887846-f0e7-1593-9422-a82adc789296', '01887846-a38a-48e1-017f-00dc5de4361a', 'CARD', '{}', 0, 86000, FALSE,
        86000, now(), now());
