export type Block = {
    componentCode: string;
    locationCode: string;
    roomCode: string;
    section: string;
    classAssociation: number;
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
};


export type Schedule = {
    blocks: Block[];
    term: string;
};
