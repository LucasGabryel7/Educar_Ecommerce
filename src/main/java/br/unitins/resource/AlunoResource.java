package br.unitins.resource;

import java.util.List;

import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.service.AlunoService;
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

@Path("/alunos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlunoResource {

    @Inject
    AlunoService service;

    @GET
    public List<AlunoResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public AlunoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/busca")
    public List<AlunoResponseDTO> buscarPorNome(@QueryParam("nome") String nome) {
        return service.buscarPorNome(nome);
    }

    @GET
    @Path("/cpf/{cpf}")
    public AlunoResponseDTO buscarPorCpf(@PathParam("cpf") String cpf) {
        return service.buscarPorCpf(cpf);
    }

    @GET
    @Path("/email/{email}")
    public AlunoResponseDTO buscarPorEmail(@PathParam("email") String email) {
        return service.buscarPorEmail(email);
    }

    @GET
    @Path("/ativos")
    public List<AlunoResponseDTO> buscarPorAtivo(@QueryParam("ativo") Boolean ativo) {
        return service.buscarPorAtivo(ativo != null ? ativo : true);
    }

    @POST
    public Response cadastrar(@Valid AlunoDTO dto) {
        AlunoResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public AlunoResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid AlunoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
