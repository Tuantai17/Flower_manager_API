package com.flower.manager.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload realtime cho ticket chat/status changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRealtimePayload {

    /**
     * ID của ticket
     */
    private Long ticketId;

    /**
     * Loại event: REPLY, STATUS, NEW_TICKET
     */
    private String type;

    /**
     * Dữ liệu kèm theo (message hoặc status info)
     */
    private Object data;
}
