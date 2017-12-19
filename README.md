# MovieRanking
It's not a Fight Club App!

Esse app foi desenvolvido utilizando desenvolvimento Android Nativo.
Sua tarefa é consumir uma API de uma comunidade de filmes chamada The Movie Database (TMDb).
Com isso, o app deverá retornar uma lista de filmes ordenada por maior nota, buscar os detalhes de um filme selecionado pelo usuário e também permitir a pesquisa de filmes.

Para isso, utilizo as seguintes tecnologias:

- Retrofit:
  Para realizar as chamadas de serviço REST da API;

- RxAndroid/RxJava:
  Reactive Programming está sendo utilizado no aplicativo para tratar os retornos das chamadas de serviço da API;

- RecyclerView e CardView:
  Para criar uma lista dinâmica de CardViews com os filmes a serem listados;
  
- Glide:
  Para buscar as imagens via URL.
    
