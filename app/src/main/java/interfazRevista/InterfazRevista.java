package interfazRevista;

import java.util.List;

import modelo.Revista;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InterfazRevista {

    @GET("issues.php")
    Call<List<Revista>> buscar(@Query("j_id") String j_id);
}
