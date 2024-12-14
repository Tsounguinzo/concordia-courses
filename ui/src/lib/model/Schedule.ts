export type Block = {
    componentCode: string;
    locationCode: string;
    roomCode: string;
    section: string;
    session: string;
    buildingCode: string;
    instructionModeCode: string;
    instructionModeDescription: string;
    mondays: string;
    tuesdays: string;
    wednesdays: string;
    thursdays: string;
    fridays: string;
    saturdays: string;
    sundays: string;
    classStartTime: string;
    classEndTime: string;
    enrollmentCapacity: number;
    currentEnrollment: number;
    waitlistCapacity: number;
    currentWaitlistTotal: number;
    hasSeatReserved: string;
};


export type Schedule = {
    blocks: Block[];
    term: string;
};
