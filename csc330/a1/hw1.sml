(* CSC 330 Assignment #1 *)
(* Oliver Tonnesen *)
(* V00885732 *)

type DATE = (int * int * int)
exception InvalidParameter

(* 1 *)
fun is_older(d1: DATE, d2: DATE): bool =
  if #1 d1 = #1 d2
  then
    if #2 d1 = #2 d2
    then
      if #3 d1 = #3 d2
      then false
      else #3 d1 < #3 d2
    else #2 d1 < #2 d2
  else #1 d1 < #1 d2

(* 2 *)
fun number_in_month(dates: DATE list, month: int): int =
  if dates <> []
  then
    if #2 (hd dates) = month
    then 1 + number_in_month(tl dates, month)
    else number_in_month(tl dates, month)
  else 0

(* 3 *)
fun number_in_months(dates: DATE list, months: int list): int =
  if months <> []
  then
    number_in_month(dates, hd months) + number_in_months(dates, tl months)
  else 0

(* 4 *)
fun dates_in_month(dates: DATE list, month: int): DATE list =
  if dates <> []
  then
    if #2 (hd dates) = month
    then
      hd dates :: dates_in_month(tl dates, month)
    else dates_in_month(tl dates, month)
  else []

(* 5 *)
fun dates_in_months(dates: DATE list, months: int list): DATE list =
  if months <> []
  then
    dates_in_month(dates, hd months)@dates_in_months(dates, tl months)
  else []

(* 6 *)
fun get_nth(strings: string list, n: int): string =
  if (n = 0) orelse (strings = [])
  then raise InvalidParameter
  else
    if n = 1
    then hd strings
    else get_nth(tl strings, n-1)

(* 7 *)
fun date_to_string(date: DATE): string =
let val months: string list =
["January","February","March","April","May","June","July","August","September",
"October","November","December"]
in
  get_nth(months, #2 date)^" "^Int.toString(#3 date)^", "^Int.toString(#1 date)
end

(* 8 *)
fun number_before_reaching_sum(sum: int, numbers: int list): int =
  if hd numbers >= sum
  then 0
  else 1 + number_before_reaching_sum(sum - hd numbers, tl numbers)

(* 9 *)
fun what_month(day: int): int =
let val days_in_month: int list =
[31,28,31,30,31,30,31,31,30,31,30,31]
in
  1 + number_before_reaching_sum(day, days_in_month)
end

(* 10 *)
fun month_range(day1: int, day2: int): int list =
  if day1 <= day2
  then what_month(day1) :: month_range(day1 + 1, day2)
  else []

(* 11 *)
fun oldest(dates: DATE list): DATE option =
  if dates <> []
  then
    if tl dates = []
    then SOME (hd dates)
    else
      if is_older(hd dates, hd (tl dates))
      then oldest(hd dates :: tl (tl dates))
      else oldest(tl dates)
  else NONE

(* 12 *)
fun reasonable_date(date: DATE): bool =
  if #1 date < 1 orelse #2 date < 1 orelse #3 date < 1 orelse #2 date > 12
  then false
  else
    if #2 date <> 2
    then
      let val days_in_month: int list =
      [31,28,31,30,31,30,31,31,30,31,30,31]
      in
        if #3 date > List.nth(days_in_month, (#2 date) - 1)
        then false
        else true
      end
    else
      (* February *)
      if (#1 date mod 400) = 0 orelse ((#1 date mod 4) = 0 andalso (#1 date mod
      100) <> 0)
      then
        (* leap year *)
        if #3 date > 29
        then false
        else true
      else
        if #3 date > 28
        then false
        else true
