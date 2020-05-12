:- lib(ic).
:- lib(ic_global).

:- compile(dados_rec).

rec_prob(Gs) :-
    %R_i #:: 0..1,
    nrects(Nrec),
    length(Rs,Nrec),   % cria uma lista Rs com os rect todos
    findall(I,v(I,_,_),Vs),
    length(Vs,S),
    length(Gs,S),     % cria uma lista que vai manter os guardas
    Z #= sum(Gs),
    Z #>= Nrec/3,
    findall(Ir,r(Ir,_,_),Rs),
    rest_Rec(Rs),
    minimize(labeling([Z|Gs]),Z).


rest_Rec([]).
rest_Rec([r|Rs]) :-
  r(_,_,Vis),
  selecionar_vert(Vis,Xv),
  sum(Xv) #>= 1,      % sum(Vis)
  rest_Rec(Rs).


selecionar_vert([],[]).
selecionar_vert([I|Vis],Xv) :-
  X_i #:: 0..1,
  v(I,_,_),
  selecionar_vert(Vis,[X_i|Xv]).
