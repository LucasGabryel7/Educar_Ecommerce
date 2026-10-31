package br.unitins.resource;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.dto.PacoteAulasDTO;
import br.unitins.dto.PacoteAulasResponseDTO;
import br.unitins.service.PacoteAulasService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pacotes-aulas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacoteAulasResource {

    @Inject
    PacoteAulasService service;

    @GET
    public List<PacoteAulasResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public PacoteAulasResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<PacoteAulasResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @GET
    @Path("/faixa-preco")
    public List<PacoteAulasResponseDTO> buscarPorFaixaPreco(
            @QueryParam("min") BigDecimal min,
            @QueryParam("max") BigDecimal max) {
        return service.buscarPorFaixaPreco(min, max);
    }

    @POST
    public Response cadastrar(@Valid PacoteAulasDTO dto) {
        PacoteAulasResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public PacoteAulasResponseDTO atualizar(@PathParam("id") Long id, @Valid PacoteAulasDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
