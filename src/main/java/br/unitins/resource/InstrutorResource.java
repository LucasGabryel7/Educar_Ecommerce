package br.unitins.resource;

import java.util.List;

import br.unitins.dto.InstrutorDTO;
import br.unitins.dto.InstrutorResponseDTO;
import br.unitins.service.InstrutorService;
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

@Path("/instrutores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InstrutorResource {

    @Inject
    InstrutorService service;

    @GET
    public List<InstrutorResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public InstrutorResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<InstrutorResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @GET
    @Path("/cpf/{cpf}")
    public InstrutorResponseDTO buscarPorCpf(@PathParam("cpf") String cpf) {
        return service.buscarPorCpf(cpf);
    }

    @GET
    @Path("/categoria/{idCategoria}")
    public List<InstrutorResponseDTO> buscarPorCategoria(@PathParam("idCategoria") Long idCategoria) {
        return service.buscarPorCategoria(idCategoria);
    }

    @GET
    @Path("/ativos")
    public List<InstrutorResponseDTO> buscarPorAtivo(@QueryParam("ativo") Boolean ativo) {
        return service.buscarPorAtivo(ativo != null ? ativo : true);
    }

    @POST
    public Response cadastrar(@Valid InstrutorDTO dto) {
        InstrutorResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public InstrutorResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid InstrutorDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
