<script lang="ts">
    import TimeblockDays from "./TimeblockDays.svelte";
    import EnrollmentStatus from "./EnrollmentStatus.svelte";
    import type {Block} from "$lib/model/Schedule";

    export let block: Block

    const timeToDisplay = (time: string) => {
        const parts = time.split('.');

        let hour = parts[0];
        let minute = parts[1];

        if (!hour || hour.length === 0) hour = '00';
        if (!minute || minute.length === 0) minute = '00';

        hour = hour.toLocaleString()
        minute = minute.toLocaleString()

        return `${hour}:${minute}`;
    };
</script>

<tr class='p-2 text-left even:bg-slate-100 even:dark:bg-[rgb(48,48,48)]'>
    <td class='whitespace-nowrap pl-4 text-sm font-semibold sm:pl-6 sm:text-base'>
        {`${block.componentCode} ${block.section}`}
    </td>
    <td class='py-2 text-gray-700 dark:text-gray-300'>
        <div class='flex flex-col items-start pl-1 text-center font-medium'>
            <EnrollmentStatus
                enrollmentCapacity={block.enrollmentCapacity}
                currentEnrollment={block.currentEnrollment}
                waitlistCapacity={block.waitlistCapacity}
                currentWaitlistTotal={block.currentWaitlistTotal}
                hasSeatReserved={block.hasSeatReserved}
            />
        </div>
    </td>
    <td class='whitespace-nowrap py-2 text-sm font-medium sm:text-base'>
        <div>
            {timeToDisplay(block.classStartTime)} - {timeToDisplay(block.classEndTime)}
        </div>
    </td>
    <td class='p-2'>
        <TimeblockDays {block}/>
    </td>
</tr>