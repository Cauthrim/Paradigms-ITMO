tree(K, V, P, S, L, R, tree(K, V, P, S, L, R)).
tree(nil, nil).
tree(K, V, T) :- rand_int(100000, Rand), tree(K, V, Rand, 1, nil, nil, T).
tree(tree(K, V, P, S, L, R), T) :- tree(K, V, P, S, L, R, T).

get_p(nil, 0).
get_p(tree(K, V, P, S, L, R), P).
get_s(nil, 0).
get_s(tree(K, V, P, S, L, R), S).

new_s(S, T, Size) :- get_s(S, S1), get_s(T, S2), Size is S1 + S2 + 1.

map_build_next([], R, R) :- !.
map_build_next([(K, V) | T], Tr, Res) :- map_put(Tr, K, V, Next), map_build_next(T, Next, Res).
map_build([], Res) :- tree(nil, Res), !.
map_build([(K, V) | T], Res) :- tree(K, V, Tr), map_build_next(T, Tr, Res). 

map_get(tree(K, V, P, S, L, R), K, V).
map_get(tree(K, V, P, S, L, R), Key, Value) :- Key < K, !, map_get(L, Key, Value).
map_get(tree(K, V, P, S, L, R), Key, Value) :- !, map_get(R, Key, Value).

split(nil, nil, nil, Key).
split(tree(K, V, P, S, L, R), Lt, Rt, Key) :- Key =< K, !, split(L, Lt, Newl, Key),
 new_s(Newl, R, News), tree(K, V, P, News, Newl, R, Rt).
split(tree(K, V, P, S, L, R), Lt, Rt, Key) :- split(R, Newr, Rt, Key),
 new_s(L, Newr, News), tree(K, V, P, News, L, Newr, Lt).

merge(nil, nil, nil) :- !.
merge(Res, L, nil) :- !, tree(L, Res).
merge(Res, nil, R) :- !, tree(R, Res).
merge(Res, tree(Lk, Lv, Lp, Ls, Ll, Lr), Rt) :- get_p(Rt, Rp), Rp < Lp, !, merge(New, Lr, Rt),
 new_s(Ll, New, News), tree(Lk, Lv, Lp, News, Ll, New, Res).
merge(Res, Lt, tree(Rk, Rv, Rp, Rs, Rl, Rr)) :- merge(New, Lt, Rl),
 new_s(New, Rr, News), tree(Rk, Rv, Rp, News, New, Rr, Res).

map_put(T, Key, Value, T) :- map_get(T, Key, Value), !.
map_put(T, Key, Value, Res) :- map_get(T, Key, _), !, map_remove(T, Key, Newt), map_put(Newt, Key, Value, Res).
map_put(T, Key, Value, Res) :- tree(Key, Value, Nt), split(T, Lt, Rt, Key), merge(Newr, Lt, Nt), merge(Res, Newr, Rt).

lower_bound(T, Key, Size) :- split(T, Tf, Ts, Key), get_s(Ts, Size).

map_subMapSize(Map, From, To, Size) :- To < From, !, Size is 0.
map_subMapSize(Map, From, To, Size) :- lower_bound(Map, From, Sl), lower_bound(Map, To, Su), Size is Sl - Su.

map_remove(T, Key, T) :- \+ map_get(T, Key, _), !.
map_remove(tree(Key, V, P, S, L, R), Key, Res) :- !, merge(Res, L, R).
map_remove(tree(K, V, P, S, L, R), Key, Res) :- K > Key, !, map_remove(L, Key, Newl),
 new_s(Newl, R, News), tree(K, V, P, News, Newl, R, Res).
map_remove(tree(K, V, P, S, L, R), Key, Res) :- map_remove(R, Key, Newr), !,
 new_s(L, Newr, News), tree(K, V, P, News, L, Newr, Res).