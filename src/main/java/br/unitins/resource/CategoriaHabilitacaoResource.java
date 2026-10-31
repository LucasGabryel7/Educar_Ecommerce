package br.unitins.resource;

import java.util.List;

import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.service.CategoriaHabilitacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/categorias-habilitacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaHabilitacaoResource {

    @Inject
    CategoriaHabilitacaoService service;

    @GET
    public List<CategoriaHabilitacaoResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public CategoriaHabilitacaoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<CategoriaHabilitacaoResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @POST
    public Response cadastrar(@Valid CategoriaHabilitacaoDTO dto) {
        CategoriaHabilitacaoResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public CategoriaHabilitacaoResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid CategoriaHabilitacaoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
