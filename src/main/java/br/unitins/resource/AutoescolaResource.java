package br.unitins.resource;

import java.util.List;

import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.service.AutoescolaService;
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

@Path("/autoescolas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AutoescolaResource {

    @Inject
    AutoescolaService service;

    @GET
    public List<AutoescolaResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public AutoescolaResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<AutoescolaResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @GET
    @Path("/cnpj/{cnpj}")
    public AutoescolaResponseDTO buscarPorCnpj(@PathParam("cnpj") String cnpj) {
        return service.buscarPorCnpj(cnpj);
    }

    @POST
    public Response cadastrar(@Valid AutoescolaDTO dto) {
        AutoescolaResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public AutoescolaResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid AutoescolaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
