package br.unitins.resource;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.AulaTeoricaResponseDTO;
import br.unitins.service.AulaTeoricaService;
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

@Path("/aulas-teoricas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AulaTeoricaResource {

    @Inject
    AulaTeoricaService service;

    @GET
    public List<AulaTeoricaResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public AulaTeoricaResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<AulaTeoricaResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @GET
    @Path("/faixa-preco")
    public List<AulaTeoricaResponseDTO> buscarPorFaixaPreco(
            @QueryParam("min") BigDecimal min,
            @QueryParam("max") BigDecimal max) {
        return service.buscarPorFaixaPreco(min, max);
    }

    @POST
    public Response cadastrar(@Valid AulaTeoricaDTO dto) {
        AulaTeoricaResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public AulaTeoricaResponseDTO atualizar(@PathParam("id") Long id, @Valid AulaTeoricaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}