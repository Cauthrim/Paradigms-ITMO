set_lpf(N, R) :- lpf(N, _), !.
set_lpf(N, R) :- assert(lpf(N, R)).

prime(N) :- N > 1, \+ lpf(N, _).
composite(N) :- N > 1, \+ prime(N).

sieve_inner_cycle(Current, Prime, N) :- Current > N, !.
sieve_inner_cycle(Current, Prime, N) :- set_lpf(Current, Prime),
 Next is Current + Prime,
 sieve_inner_cycle(Next,  Prime, N).

sieve_buffer(Current, N) :- composite(Current), !.
sieve_buffer(Current, N) :- Square is Current * Current, sieve_inner_cycle(Square, Current, N).
sieve(Current, N) :- Current * Current > N, !.
sieve(Current, N) :- sieve_buffer(Current, N), Next is Current + 1, sieve(Next, N).


prime_divisors(1, []). 
prime_divisors(N, [N]) :- prime(N), !.
prime_divisors(N, [H | T]) :- number(N), !, lpf(N, H), Div is N / H, prime_divisors(Div, T).
prime_divisors(N, [F, S | T]) :- var(N), !, F =< S, prime(F), prime_divisors(Next, [S | T]), N is Next * F.

copy_list([], []).
copy_list([F | T1], [F | T2]) :- copy_list(T1, T2).

accumulate_divisors([], [], Res) :- prime_divisors(V, Res), !.
accumulate_divisors([], Div2, Div2).
accumulate_divisors(Div1, [], Div1).

accumulate_divisors([F1 | T1], [F2 | T2], Res) :- F1 < F2,
 accumulate_divisors(T1, [F2 | T2], Next),
 copy_list(Res, [F1 | Next]).

accumulate_divisors([F1 | T1], [F2 | T2], Res) :- F2 < F1,
 accumulate_divisors([F1 | T1], T2, Next), 
 copy_list(Res, [F2 | Next]).

accumulate_divisors([F | T1], [F | T2], Res) :- accumulate_divisors(T1, T2, Next), copy_list(Res, [F | Next]).

lcm(A, B, LCM) :- prime_divisors(A, Div1), prime_divisors(B, Div2),
 accumulate_divisors(Div1, Div2, DivLcm), 
 prime_divisors(LCM, DivLcm).

init(N) :- sieve(2, N).
