package br.unitins.resource;

import java.util.List;

import br.unitins.dto.PedidoDTO;
import br.unitins.dto.PedidoResponseDTO;
import br.unitins.dto.StatusPedidoDTO;
import br.unitins.model.StatusPedido;
import br.unitins.service.PedidoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoService service;

    @GET
    public List<PedidoResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public PedidoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/aluno/{alunoId}")
    public List<PedidoResponseDTO> buscarPorAluno(@PathParam("alunoId") Long alunoId) {
        return service.buscarPorAluno(alunoId);
    }

    @GET
    @Path("/status/{status}")
    public List<PedidoResponseDTO> buscarPorStatus(@PathParam("status") String status) {
        return service.buscarPorStatus(status);
    }

    @POST
    public Response cadastrar(@Valid PedidoDTO dto) {
        PedidoResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PATCH
    @Path("/{id}/status")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    public PedidoResponseDTO atualizarStatus(
            @PathParam("id") Long id,
            @QueryParam("status") String statusQuery,
            @Valid StatusPedidoDTO dto) {
        if (statusQuery != null && !statusQuery.isBlank()) {
            return service.atualizarStatus(id, statusQuery);
        }
        StatusPedido status = (dto != null) ? dto.status() : null;
        return service.atualizarStatus(id, status);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
